package org.eclipse.che.plugin.languageserver.server;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.che.plugin.languageserver.shared.lsapi.DidChangeTextDocumentParamsDTO;
import org.eclipse.che.plugin.languageserver.shared.lsapi.DidCloseTextDocumentParamsDTO;
import org.eclipse.che.plugin.languageserver.shared.lsapi.DidOpenTextDocumentParamsDTO;
import org.eclipse.che.plugin.languageserver.shared.lsapi.DidSaveTextDocumentParamsDTO;
import org.eclipse.che.plugin.languageserver.shared.lsapi.TextDocumentPositionParamsDTO;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.typefox.lsapi.CompletionItem;
import io.typefox.lsapi.LanguageServer;

/**
 * REST API for the textDocument/* services defined in https://github.com/Microsoft/vscode-languageserver-protocol
 * Dispatches onto the {@link LanguageServerRegistry}.
 */
@Singleton
@Path("languageserver/textDocument")
public class TextDocumentServiceImpl {

    LanguageServerRegistry languageServerRegistry;

    @Inject
    public TextDocumentServiceImpl(LanguageServerRegistry languageServerRegistry) {
        this.languageServerRegistry = languageServerRegistry;
    }

    @POST
    @Path("completion")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<? extends CompletionItem> completion(TextDocumentPositionParamsDTO textDocumentPositionParams) {
        LanguageServer server = getServer(textDocumentPositionParams.getTextDocument().getUri());
        List<? extends CompletionItem> completion = server.getTextDocumentService()
                .completion(textDocumentPositionParams);
        return completion;
    }

    @POST
    @Path("didChange")
    @Consumes(MediaType.APPLICATION_JSON)
    public void didChange(DidChangeTextDocumentParamsDTO change) {
        LanguageServer server = getServer(change.getTextDocument().getUri());
        server.getTextDocumentService().didChange(change);
    }

    @POST
    @Path("didOpen")
    @Consumes(MediaType.APPLICATION_JSON)
    public void didOpen(DidOpenTextDocumentParamsDTO openEvent) {
        LanguageServer server = getServer(openEvent.getTextDocument().getUri());
        server.getTextDocumentService().didOpen(openEvent);
    }

    @POST
    @Path("didClose")
    @Consumes(MediaType.APPLICATION_JSON)
    public void didClose(DidCloseTextDocumentParamsDTO closeEvent) {
        LanguageServer server = getServer(closeEvent.getTextDocument().getUri());
        server.getTextDocumentService().didClose(closeEvent);
    }

    @POST
    @Path("didSave")
    @Consumes(MediaType.APPLICATION_JSON)
    public void didSave(DidSaveTextDocumentParamsDTO saveEvent) {
        LanguageServer server = getServer(saveEvent.getTextDocument().getUri());
        server.getTextDocumentService().didSave(saveEvent);
    }

    private LanguageServer getServer(String uri) {
        LanguageServer server = languageServerRegistry.findServer(uri);
        if (server == null) {
            throw new IllegalStateException("Couldn't find registered language server for path '" + uri + "'.");
        }
        return server;
    }
}